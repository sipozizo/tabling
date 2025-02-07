# Spring Plus Project - 테이블링 따라잡기
   프로젝트 기간 : 2025.02.03 - 2025.02.07
<br>

## 📄 목차   
1. [프로젝트 개요](#프로젝트-개요)
2. [주요 기능](#주요-기능)
3. [와이어 프레임](#와이어-프레임)
4. [ERD](#ERD)
5. [API 명세서](#API-명세서)
<br>   

## 프로젝트 개요 
식당 예약 어플리케이션 테이블링을 벤치마킹하여 밴엔드 성능 개선을 추구
### 핵심 KPI
* 대용량 데이터처리(Dummy Data 500만건) 시 걸리는 시간을 측정하고 개선
* 캐시 미적용 시 성능과 캐시 적용 후 성능을 테스트 해 비교
  - Store 기능 중 단건 조회와 전체 목록 조회, 인기검색어 기능에 캐싱을 적용해 미적용 상황일 때와 비교 테스트 
* 동시성 문제 상황 유발 및 문제 해결
  - Reservation 기능 사용 시 동시성 이슈 테스트 및 문제 해결 
 

### 사용된 기술
* Java 17
* Spring boot: 3.4.2
  * Spring-Security
  * Spring-Data-Jpa
  * Spring-Cache (Caffeine Cache)
* Redis
* Lombok
* MySQL
* BCrypt: 0.10.2
* JWT
* JUnit
* Jackson Datatype JSR310

### 주요 기능
- 회원 가입 (사장/고객)
- 로그인
- 가게 CR - (non caching / caching applied)
- 예약 CR - (lock)

## 와이어 프레임  
![Image](https://github.com/user-attachments/assets/52c914a2-40bf-487e-b6c7-8347d60b540f)

## ERD
![Image](https://github.com/user-attachments/assets/7264bf89-911d-4f25-a41f-320db1fa6707)

## API 명세서
![Image](https://github.com/user-attachments/assets/c7a79d2f-7896-4a9d-88cd-c386cc924d51)

   
## Members

<markdown-accessiblity-table data-catalyst=""><table align="center">
    <thead>
        <tr>
            <th>👑 팀장</th>
            <th>팀원</th>
            <th>팀원</th>
            <th>팀원</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="center"><a href="https://github.com/Revengersy"><img src="https://github.com/user-attachments/assets/774a717d-f831-4bac-babd-fab0036be014" width="100px;" alt="" style="max-width: 100%;"></a></td>
            <td align="center"><a href="https://github.com/KyeongranMun"><img src="https://github.com/user-attachments/assets/998f392e-b8c4-4c90-8e6c-d0e0ce0fc467" width="100px;" alt="" style="max-width: 100%;"></a></td>
            <td align="center"><a href="https://github.com/bottle1000"><img src="https://github.com/user-attachments/assets/82715fcd-b769-43d4-9f01-00a5775f1ed9" width="100px;" alt="" style="max-width: 100%;"></a></td>
            <td align="center"><a href="https://github.com/TrainH"><img src="https://github.com/user-attachments/assets/3bf0d693-dacf-49a2-9764-9e7073ca1725" width="100px;" alt="" style="max-width: 100%;"></a></td>
        </tr>
        <tr>
            <td align="center">최원준</td>
            <td align="center">문경란</td>
            <td align="center">박병천</td>
            <td align="center">한교범</td>
        </tr>
    </tbody>
</table></markdown-accessiblity-table>

## Visit Here!   
### General links
- [🚗 Visit Our Repo](https://github.com/sipozizo/tabling)   
- [🙋‍♂️ Visit Our Notion](https://www.notion.so/teamsparta/13395675b31d47388ead2a64555faeba)
