# 프로젝트 SAI(Social Activity Interpreter)
### * 프로젝트 SAI의 백엔드 개발 Repository 입니다.

### * 사이(SAI) RestAPI 사용 설명서
- 사이 RestAPI 사용 설명서는 아래의 링크를 확인해주세요!
- [사이 RestAPI 사용 설명서](https://devraphy.tistory.com/640)

### * 개발 명세 및 보고서
- 사이의 개발 현황이 궁금하시다면 아래의 링크를 확인해주세요!
- [사이 개발 명세 및 보고서 바로가기](https://docs.google.com/spreadsheets/d/1BZaCrvZ1CDQfG-mHz1vHmHRu7D0na4q504e9GBBEco8/edit?usp=sharing)

### * 개발 이야기
- 개발 과정에서 다음과 같은 문제를 마주하고 해결했습니다!
- [JoinTable 전략의 문제점과 영속 상태에 대한 이해](https://devraphy.tistory.com/632)
- [Update Query를 직접 작성한다면(feat. 타입 오류)](https://devraphy.tistory.com/633)
- [WebSecurityConfigurerAdapter가 Deprecated 되었다!](https://devraphy.tistory.com/636)
- [DataIntegrityValidationException은 어디서 처리할까?](https://devraphy.tistory.com/638)

<hr>

## 사이(SAI) 소개


### a) 프로젝트 구조 및 기술 스택
- Spring Boot, Spring JPA, Spring Security
- JWT, OpenAPI, Swagger
- MySQL, Heroku

</br>

### b) 개발 동기 - 해결하려는 문제 및 비즈니스
- 본 개발자는 사람을 만나는 것에 에너지를 소비하는 성향으로, 사람 간의 일을 잘 기억하지 못합니다.
- 그러나 사소한 것에 대한 기억을 공유하는 것이야말로 관계를 발전시키는 방법입니다.
- 이와 같은 이유로 만남을 기록하고 리뷰하여 관계를 더욱 편리하게 관리할 수 있는 서비스, SAI를 개발하게 되었습니다.

</br>

### c) 사이의 뜻
- 사이는 너와 나의 사이에서 착안된 이름으로, 사람 간의 관계를 의미합니다.
- SAI는 Social Activity Interpreter의 약자로, 사회 활동을 기반하여 관계를 해석함을 의미합니다. 

</br>

### d) 서비스의 목적
- 사이는 사람과의 만남을 기록 및 리뷰하고, 이를 기반으로 관계를 수치화하는 웹 애플리케이션입니다.
- 무엇인가 기록하는 도구는 대부분 미래 중점적으로 사용됩니다. 대표적으로 플래너가 있습니다.  
- 그러나 SAI는 일기장처럼 과거에 발생한 일을 기록하는 용도로 사용합니다.
- 과거의 일을 기반으로, CRM과 같이 관계 수치화 서비스를 제공하는 것에 목적을 두었습니다. 
