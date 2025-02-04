package sipozizo.tabling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sipozizo.tabling.common.entity.*;
import sipozizo.tabling.domain.reservation.enums.ReservationStatus;
import sipozizo.tabling.domain.reservation.repository.ReservationRepository;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV1;
import sipozizo.tabling.domain.reservation.service.ReservationServiceV2;
import sipozizo.tabling.domain.store.repository.StoreRepository;
import sipozizo.tabling.domain.user.enums.UserRole;
import sipozizo.tabling.domain.user.repository.UserRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
class TablingApplicationTests {

    @Autowired
    private ReservationServiceV1 reservationServiceV1;

    @Autowired
    private ReservationServiceV2 reservationServiceV2;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void testConcurrentReservationStatusUpdateV1() throws InterruptedException, ExecutionException {
        System.out.println("== V1 테스트 시작: 동시성 제어 미적용 ==");

        // 테스트 데이터를 생성합니다.
        Reservation reservation = createTestData();

        // 쓰레드 수
        int threadCount = 4;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        // 작업 리스트
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            Future<Void> future = executor.submit(() -> {
                try {
                    barrier.await(); // 동시에 시작
                    updateReservationV1(reservation.getId(), index);
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("V1 - 쓰레드 " + index + "에서 예외 발생: " + e.getMessage());
                }
                return null;
            });
            futures.add(future);
        }

        // 모든 작업 완료 대기
        for (Future<Void> future : futures) {
            future.get();
        }

        // 최종 예약 상태 확인
        Reservation finalReservation = reservationRepository.findById(reservation.getId()).orElse(null);
        System.out.println("V1 - 최종 예약 상태: " + finalReservation.getReservationStatus());

        executor.shutdown();
    }

    @Test
    public void testConcurrentReservationStatusUpdateV2() throws InterruptedException, ExecutionException {
        System.out.println("== V2 테스트 시작: 분산 락 적용 ==");

        // 테스트 데이터를 생성합니다.
        Reservation reservation = createTestData();

        // 쓰레드 수
        int threadCount = 4;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        // 작업 리스트
        List<Future<Void>> futures = new ArrayList<>();

        final List<String> successfulThreads = new ArrayList<>();
        final List<String> failedThreads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            Future<Void> future = executor.submit(() -> {
                try {
                    barrier.await(); // 동시에 시작
                    updateReservationV2(reservation.getId(), index);
                    successfulThreads.add("쓰레드 " + index);
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // Lock 획득 실패 시 예외 처리
                    System.out.println("V2 - 쓰레드 " + index + "에서 Lock 획득 실패: " + e.getMessage());
                    failedThreads.add("쓰레드 " + index);
                } catch (Exception e) {
                    System.out.println("V2 - 쓰레드 " + index + "에서 예외 발생: " + e.getMessage());
                }
                return null;
            });
            futures.add(future);
        }

        // 모든 작업 완료 대기
        for (Future<Void> future : futures) {
            future.get();
        }

        // 최종 예약 상태 확인
        Reservation finalReservation = reservationRepository.findById(reservation.getId()).orElse(null);
        System.out.println("V2 - 최종 예약 상태: " + finalReservation.getReservationStatus());

        System.out.println("V2 - 성공한 쓰레드: " + successfulThreads);
        System.out.println("V2 - 실패한 쓰레드: " + failedThreads);

        executor.shutdown();
    }

    private Reservation createTestData() {
        // Store Owner 생성
        String ownerEmail = "owner" + System.currentTimeMillis() + "@example.com";
        User owner = new User("스토어 오너", "010-9999-8888", "스토어 주소", ownerEmail, "password", UserRole.OWNER);
        userRepository.save(owner);

        // Store 생성 (createTestStore 메서드 사용)
        Store store = createTestStore(
                "테스트 스토어",
                "02-1234-5678",
                "서울시 테스트구 테스트동",
                "123-45-67890",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                10 // maxSeatingCapacity (최대 착석 인원 설정)
        );
        storeRepository.save(store);

        // 예약자 User 생성
        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";
        User user = new User("테스트 사용자", "010-1234-5678", "테스트 주소", uniqueEmail, "password", UserRole.USER);
        userRepository.save(user);

        // 예약 생성
        Reservation reservationV1 = reservationServiceV1.createReservation(user.getId(), store.getId());
        Reservation reservationV2 = reservationServiceV2.createReservation(user.getId(), store.getId());

        // 동일한 예약 ID를 반환하여 각 테스트에서 사용할 수 있도록 합니다.
        // 여기서는 V1의 예약을 사용합니다.
        return reservationV1;
    }

    public void updateReservationV1(Long reservationId, int threadIndex) {
        // 각 쓰레드는 서로 다른 상태로 예약을 업데이트 시도
        ReservationStatus[] statuses = {
                ReservationStatus.SEATED,
                ReservationStatus.CALLED,
                ReservationStatus.EMPTIED,
                ReservationStatus.CANCELLED,
                ReservationStatus.WAITING
        };
        ReservationStatus status = statuses[threadIndex % statuses.length];

        reservationServiceV1.updateReservationStatus(reservationId, status);
        System.out.println("V1 - 쓰레드 " + threadIndex + "에서 예약 상태를 " + status + "(으)로 변경 시도");
    }

    public void updateReservationV2(Long reservationId, int threadIndex) {
        // 각 쓰레드는 서로 다른 상태로 예약을 업데이트 시도
        ReservationStatus[] statuses = {
                ReservationStatus.SEATED,
                ReservationStatus.CALLED,
                ReservationStatus.EMPTIED,
                ReservationStatus.CANCELLED,
                ReservationStatus.WAITING
        };
        ReservationStatus status = statuses[threadIndex % statuses.length];

        reservationServiceV2.updateReservationStatus(reservationId, status);
        System.out.println("V2 - 쓰레드 " + threadIndex + "에서 예약 상태를 " + status + "(으)로 변경 시도");
    }


    @Test
    public void testPerformanceV1() throws InterruptedException, ExecutionException {
        System.out.println("== V1 성능 테스트 시작: 동시성 제어 미적용 ==");

        // 테스트 데이터를 생성합니다.
        Store store = createTestStore();
        List<User> users = createTestUsers(400); // n명의 사용자 생성

        int threadCount = 100;

        ExecutorService executor = Executors.newFixedThreadPool(100); // 스레드 풀 크기 제한
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        List<Future<Void>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final User user = users.get(i);
            Future<Void> future = executor.submit(() -> {
                try {
                    barrier.await(); // 동시에 시작
                    // 예약 생성
                    reservationServiceV1.createReservation(user.getId(), store.getId());
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("V1 - 쓰레드 " + index + "에서 예외 발생: " + e.getMessage());
                }
                return null;
            });
            futures.add(future);
        }

        // 모든 작업 완료 대기
        for (Future<Void> future : futures) {
            future.get();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("V1 - 전체 실행 시간: " + (endTime - startTime) + "ms");

        executor.shutdown();
    }

    @Test
    public void testPerformanceV2() throws InterruptedException, ExecutionException {
        System.out.println("== V2 성능 테스트 시작: 분산 락 적용 ==");

        // 테스트 데이터를 생성합니다.
        Store store = createTestStore();
        List<User> users = createTestUsers(400); // 1000명의 사용자 생성

        int threadCount = 100;

        ExecutorService executor = Executors.newFixedThreadPool(100); // 스레드 풀 크기 제한
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        List<Future<Void>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            final User user = users.get(i);
            Future<Void> future = executor.submit(() -> {
                try {
                    barrier.await(); // 동시에 시작
                    // 예약 생성
                    reservationServiceV2.createReservation(user.getId(), store.getId());
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("V2 - 쓰레드 " + index + "에서 예외 발생: " + e.getMessage());
                }
                return null;
            });
            futures.add(future);
        }

        // 모든 작업 완료 대기
        for (Future<Void> future : futures) {
            future.get();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("V2 - 전체 실행 시간: " + (endTime - startTime) + "ms");

        executor.shutdown();
    }

    private Store createTestStore() {
        // Store Owner 생성
        String ownerEmail = "owner" + System.currentTimeMillis() + "@example.com";
        User owner = new User("스토어 오너", "010-9999-8888", "스토어 주소", ownerEmail, "password", UserRole.OWNER);
        userRepository.save(owner);

        // Store 생성
        Store store = createTestStore(
                "테스트 스토어",
                "02-1234-5678",
                "서울시 테스트구 테스트동",
                "123-45-67890",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                100 // 최대 착석 인원 설정 (테스트를 위해 큰 값 설정)
        );
        storeRepository.save(store);

        return store;
    }

    private List<User> createTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String uniqueEmail = "user" + System.currentTimeMillis() + "_" + i + "@example.com";
            User user = new User("테스트 사용자" + i, "010-1000-1" + String.format("%03d", i), "테스트 주소", uniqueEmail, "password", UserRole.USER);
            userRepository.save(user);
            users.add(user);
        }
        return users;
    }

    private Store createTestStore(String storeName, String storeNumber, String storeAddress,
                                        String registrationNumber, LocalTime openingTime,
                                        LocalTime closingTime, int maxSeatingCapacity) {
        return Store.builder()
                .storeName(storeName)
                .storeNumber(storeNumber)
                .storeAddress(storeAddress)
                .registrationNumber(registrationNumber)
                .openingTime(openingTime)
                .closingTime(closingTime)
                .maxSeatingCapacity(maxSeatingCapacity)
                .build();
    }

}