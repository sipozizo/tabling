package sipozizo.tabling;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sipozizo.tabling.common.entity.Reservation;
import sipozizo.tabling.common.entity.Store;
import sipozizo.tabling.common.entity.User;
import sipozizo.tabling.common.entity.UserRole;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV1;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV2;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV3;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.domain.user.repository;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test") // 테스트 프로파일 지정
public class TablingWaitingNumTests {

    @Autowired
    private ReservationServiceV1 reservationServiceV1;

    @Autowired
    private ReservationServiceV2 reservationServiceV2;

    @Autowired
    private ReservationServiceV3 reservationServiceV3;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @AfterEach
    public void tearDown() {
        reservationRepository.deleteAllInBatch();
        storeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    public void testV1ConcurrentReservations() throws InterruptedException {
        // 데이터베이스에 저장된 User 수 확인
        long userCount = userRepository.count();

        // 매장 소유자 User 생성
        String ownerEmail = "owner" + (userCount + 1) + "@example.com";
        User storeOwner = new User(
                "Owner" + (userCount + 1),     // name
                "010-0000-0000",              // phoneNumber
                "Owner Address",              // address
                ownerEmail,                   // email
                "password123",                // password
                UserRole.USER                 // userRole
        );
        userRepository.save(storeOwner);

        // 매장 생성
        Store store = Store.createTestStore(
                storeOwner,                     // user (매장 주인)
                "Test Store",                   // name
                "02-1234-5678",                 // storeNumber
                "Store Address",                // address
                "123-45-67890",                 // registrationNumber
                0,                              // view
                LocalTime.of(9, 0),             // openingTime
                LocalTime.of(18, 0),            // closingTime
                50                              // maxSeatingCapacity
        );
        storeRepository.save(store);

        // 예약할 고객 User 생성
        String reserverEmail = "reserver" + (userCount + 2) + "@example.com";
        User reserver = new User(
                "Reserver" + (userCount + 2),   // name
                "010-1234-5678",                // phoneNumber
                "Reserver Address",             // address
                reserverEmail,                  // email
                "password123",                  // password
                UserRole.USER                   // userRole
        );
        userRepository.save(reserver);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 예약 생성 작업
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationServiceV1.createReservation(reserver.getId(), store.getId());
                } catch (Exception e) {
                    e.printStackTrace(); // 스택 트레이스 출력
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 모든 예약 가져오기
        List<Reservation> reservations = reservationRepository.findAll();

        // 중복된 대기번호 확인
        Map<Integer, Integer> waitingNumberCount = new HashMap<>();
        for (Reservation reservation : reservations) {
            Integer waitingNumber = reservation.getWaitingNumber();
            waitingNumberCount.put(waitingNumber, waitingNumberCount.getOrDefault(waitingNumber, 0) + 1);
        }

        // 중복된 대기번호 수집
        Set<Integer> duplicateWaitingNumbers = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : waitingNumberCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateWaitingNumbers.add(entry.getKey());
            }
        }

        System.out.println("Total reservations: " + reservations.size());
        System.out.println("Unique waiting numbers: " + waitingNumberCount.size());
        System.out.println("Duplicate waiting numbers: " + duplicateWaitingNumbers);

        // Assert를 통해 중복 여부 확인
//        assertThat(duplicateWaitingNumbers.size()).isGreaterThan(0);
    }

    @Test
    public void testV2ConcurrentReservations() throws InterruptedException {
        // 데이터베이스에 저장된 User 수 확인
        long userCount = userRepository.count();

        // 매장 소유자 User 생성
        String ownerEmail = "owner" + (userCount + 1) + "@example.com";
        User storeOwner = new User(
                "Owner" + (userCount + 1),     // name
                "010-0000-0000",              // phoneNumber
                "Owner Address",              // address
                ownerEmail,                   // email
                "password123",                // password
                UserRole.USER                 // userRole
        );
        userRepository.save(storeOwner);

        // 매장 생성
        Store store = Store.createTestStore(
                storeOwner,                     // user (매장 주인)
                "Test Store",                   // name
                "02-1234-5678",                 // storeNumber
                "Store Address",                // address
                "123-45-67890",                 // registrationNumber
                0,                              // view
                LocalTime.of(9, 0),             // openingTime
                LocalTime.of(18, 0),            // closingTime
                50                              // maxSeatingCapacity
        );
        storeRepository.save(store);

        // 예약할 고객 User 생성
        String reserverEmail = "reserver" + (userCount + 2) + "@example.com";
        User reserver = new User(
                "Reserver" + (userCount + 2),   // name
                "010-1234-5678",                // phoneNumber
                "Reserver Address",             // address
                reserverEmail,                  // email
                "password123",                  // password
                UserRole.USER                   // userRole
        );
        userRepository.save(reserver);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 예약 생성 작업
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationServiceV2.createReservation(reserver.getId(), store.getId());
                } catch (Exception e) {
                    e.printStackTrace(); // 스택 트레이스 출력
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 모든 예약 가져오기
        List<Reservation> reservations = reservationRepository.findAll();

        // 중복된 대기번호 확인
        Map<Integer, Integer> waitingNumberCount = new HashMap<>();
        for (Reservation reservation : reservations) {
            Integer waitingNumber = reservation.getWaitingNumber();
            waitingNumberCount.put(waitingNumber, waitingNumberCount.getOrDefault(waitingNumber, 0) + 1);
        }

        // 중복된 대기번호 수집
        Set<Integer> duplicateWaitingNumbers = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : waitingNumberCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateWaitingNumbers.add(entry.getKey());
            }
        }

        System.out.println("Total reservations: " + reservations.size());
        System.out.println("Unique waiting numbers: " + waitingNumberCount.size());
        System.out.println("Duplicate waiting numbers: " + duplicateWaitingNumbers);

        // Assert를 통해 중복 여부 확인
//        assertThat(duplicateWaitingNumbers.size()).isEqualTo(0);
    }


    @Test
    public void testV3ConcurrentReservations() throws InterruptedException {
        // 데이터베이스에 저장된 User 수 확인
        long userCount = userRepository.count();

        // 매장 소유자 User 생성
        String ownerEmail = "owner" + (userCount + 1) + "@example.com";
        User storeOwner = new User(
                "Owner" + (userCount + 1),     // name
                "010-0000-0000",              // phoneNumber
                "Owner Address",              // address
                ownerEmail,                   // email
                "password123",                // password
                UserRole.USER                 // userRole
        );
        userRepository.save(storeOwner);

        // 매장 생성
        Store store = Store.createTestStore(
                storeOwner,                     // user (매장 주인)
                "Test Store",                   // name
                "02-1234-5678",                 // storeNumber
                "Store Address",                // address
                "123-45-67890",                 // registrationNumber
                0,                              // view
                LocalTime.of(9, 0),             // openingTime
                LocalTime.of(18, 0),            // closingTime
                50                              // maxSeatingCapacity
        );
        storeRepository.save(store);

        // 예약할 고객 User 생성
        String reserverEmail = "reserver" + (userCount + 2) + "@example.com";
        User reserver = new User(
                "Reserver" + (userCount + 2),   // name
                "010-1234-5678",                // phoneNumber
                "Reserver Address",             // address
                reserverEmail,                  // email
                "password123",                  // password
                UserRole.USER                   // userRole
        );
        userRepository.save(reserver);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 예약 생성 작업
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationServiceV3.createReservation(reserver.getId(), store.getId());
                } catch (Exception e) {
                    e.printStackTrace(); // 스택 트레이스 출력
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 모든 예약 가져오기
        List<Reservation> reservations = reservationRepository.findAll();

        // 중복된 대기번호 확인
        Map<Integer, Integer> waitingNumberCount = new HashMap<>();
        for (Reservation reservation : reservations) {
            Integer waitingNumber = reservation.getWaitingNumber();
            waitingNumberCount.put(waitingNumber, waitingNumberCount.getOrDefault(waitingNumber, 0) + 1);
        }

        // 중복된 대기번호 수집
        Set<Integer> duplicateWaitingNumbers = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : waitingNumberCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateWaitingNumbers.add(entry.getKey());
            }
        }

        System.out.println("Total reservations: " + reservations.size());
        System.out.println("Unique waiting numbers: " + waitingNumberCount.size());
        System.out.println("Duplicate waiting numbers: " + duplicateWaitingNumbers);

        // Assert를 통해 중복 여부 확인
//        assertThat(duplicateWaitingNumbers.size()).isEqualTo(0);
    }
}
