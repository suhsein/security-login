# Security Login
_Spring Security와 JWT, OAuth2를 활용하여 개발한 통합 로그인 환경입니다._

스프링 시큐리티의 동작 과정을 이해하고, 필터와 핸들러를 커스텀하여 폼 로그인 환경과 소셜 로그인 환경을 통합한 통합 로그인 환경을 구축했습니다.

> 스프링 시큐리티의 동작 과정에 대한 정리본은 https://velog.io/@suhsein/series/Spring-Security 에서 확인할 수 있습니다.

> 이 프로젝트의 시연 동영상은 https://youtu.be/ow-R_BTQjrA 에서 확인하실 수 있습니다. 

![securityfilterchain](https://github.com/suhsein/security-login/assets/76998096/472f93c7-782c-44dd-85a8-83e47c1d64a3)

백엔드가 요청에 응답하는 방식은 총 두가지로, RestController를 통해 응답하거나 스프링 시큐리티 필터를 통해서 응답하고 있습니다.
현재 로그인 요청, 로그아웃 요청과 관련해서는 스프링 시큐리티 필터를 통해서 응답하도록 하였습니다.
시큐리티 필터를 통해 응답하는 경우에는 주로 커스텀 핸들러, 필터를 구현해서 사용했습니다.
이를 통하여 불필요한 디스패처 서블릿의 호출을 줄이고, 요청에 적합한 보안 및 데이터 처리를 하여 유연한 응답을 할 수 있었습니다.

## JWT 방식
Http는 기본적으로 stateless로 동작하기 때문에, **서버가 로그인된 사용자를 기억하고 상태를 유지**하기 위해서 주로 `세션 방식`을 사용합니다.
로그인된 사용자가 많아진다면, 수많을 세션을 관리하기 위해 서버 스케일 아웃과 관련한 고민을 해야합니다.
다중 서버에서는 따로 세션 스토리지를 두고 세션을 관리합니다.
서버와의 상호작용이 잦은 SSR(Server Side Rendering)의 경우 주로 세션 방식을 사용합니다.

반면 JWT(Json Web Token)을 사용하는 경우에는 서버는 세션을 저장하지 않고 메타 데이터를 담은 토큰을 사용자에게 전달하고, 사용자가 이를 저장하여 다음 요청에 사용합니다.
JWT 방식은 **서버의 부하를 줄이고 클라이언트 측으로 부하를 분산시킬 수 있다는 장점**이 있습니다.
또한 서버는 상태를 기억하지 않기 때문에 stateless하게 동작합니다.
서버와의 상호작용이 잦지 않은 CSR(Client Side Rendering)의 경우 주로 JWT 방식을 사용합니다.

JWT 방식을 사용하기 위해 CSR 방식으로 프론트엔드를 구현했습니다.

또한 access, refresh 토큰을 이중으로 사용하며 `refresh rotate`을 적용하여 액세스 토큰 재발급 요청마다 리프레시 토큰도 재발급할 수 있도록 구현했습니다.

### 주의사항
JWT 사용 시 보안상 위험한 정보를 담지 않는 것이 중요합니다.
페이로드와 헤더의 정보들은 암호화되지 않고 그대로 노출되기 때문에 인가에 꼭 필요한 메타데이터만을 담고, 서명은 서버에서 비밀키를 생성하여 서명하도록 했습니다.

### 토큰의 저장소
`local storage`는 XSS 공격에는 취약하지만 쿠키처럼 자동 전송되지 않기 때문에 CSRF 공격에는 상대적으로 안전합니다.
`쿠키`는 httpOnly를 적용하여 XSS 공격을 방지할 수 있지만 자동 전송 되기 때문에 CSRF 공격에는 취약합니다.

`access 토큰`의 경우 CRUD에 사용되기 때문에 **CSRF 공격에 민감**하게 대응할 필요성이 있습니다. 그렇기 때문에 access 토큰은 **local storage에 저장**했습니다. 물론 XSS 공격을 방지하기 위한 추가적인 설정이 필요합니다.

`refresh 토큰`의 경우 오직 access 토큰의 재발급만을 위해서 사용되기 때문에 CSRF 공격에 상대적으로 민감하지 않습니다. 그렇기 때문에 refresh 토큰은 **httpOnly를 적용한 쿠키**에 저장합니다.

## OAuth2 방식
OAuth2는 알려진 외부 사이트의 사용자 계정을 통해 인증을 위임하기 위해 사용합니다. 우리 측 서버에서는 외부 서비스로 인증을 요청하고, 외부 서비스의 인증 서버는 인증을 한 후 인증코드를 응답합니다. 인증코드를 사용하여 인증 서버로 요청하면 토큰을 응답받게 됩니다. 토큰을 사용하여 리소스 서버로 요청하면 사용자 정보를 응답받을 수 있습니다.

_외부 서비스로의 인증 과정은 프론트엔드와 백엔드가 분할해서 처리하는 경우 보안상의 문제가 발생할 수 있기 때문에 백엔드에서 모든 처리를 한 후 프론트에게 정제된 리소스를 응답하도록 합니다._

OAuth2를 사용하는 가장 큰 이유는 **사용자가 신뢰할 수 있는 사이트로부터 인증을 하기 때문에 사용자 경험을 향상할 수 있다는 점**입니다.

다만 OAuth2를 사용할 때 외부 서비스의 서버에 문제가 발생할 경우 인증이 불가능하다는 한계점 또한 존재하기 때문에, 폼 로그인과 OAuth2를 함께 사용하는 방식으로 구현했습니다.

## 기술스택

<h3 align="center"> 인증 </h3>

<div align="center">
  <img alt="OAuth2" src="https://img.shields.io/badge/OAuth2-000000?style=for-the-badge&logoColor=white">&nbsp
  <img alt="JWT" src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logoColor=white">&nbsp
</div>

<h3 align="center"> Back-end </h3>

<div align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white">&nbsp
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=black">&nbsp
  <img alt="SpringDataJPA" src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">&nbsp<br>
  <img alt="Spring" src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">&nbsp
  <img alt="SpringBoot" src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">&nbsp
  <img alt="SpringSecurity" src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">&nbsp
</div>

<h3 align="center"> Front-end (CSR) </h3>

<div align="center">
  <img alt="React"src="https://img.shields.io/badge/React-61DAFB.svg?style=for-the-badge&logo=React&logoColor=20232a" />&nbsp
  <img alt="Javascript"src="https://img.shields.io/badge/Javascript-F7DF1E.svg?style=for-the-badge&logo=Javascript&logoColor=20232a" />&nbsp
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"/>&nbsp
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=CSS3&logoColor=white"/>&nbsp
</div>

<h3 align="center"> Tools </h3>

<div align="center">
  <img src="https://img.shields.io/badge/IntelliJ IDEA-000000.svg?style=for-the-badge&logo=intellijidea&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/VSCode-2C2C32.svg?style=for-the-badge&logo=visual-studio-code&logoColor=22ABF3" />&nbsp
</div>


# Back-end

## Configuration
시큐리티 관련 설정들을 위한 configuration을 구현했습니다. 커스텀 필터와 핸들러를 등록하고, 프론트엔드와의 리소스 공유를 위한 CORS 설정을 했습니다.
또한 특정 url들에 대한 인가처리를 하여 인가되지 않은 사용자와 인가된 사용자가 접근할 수 있는 페이지를 구분하였습니다.
현재 세션 방식이 아닌 JWT 방식의 인증을 사용하기 때문에 csrf과 session 사용과 관련한 설정을 disable 했습니다.

또한 인증에 실패하는 경우 프론트엔드로 인가되지 않은 사용자임을 알리기 위해 401 코드를 응답하도록 실패 핸들러를 구현하여 빈으로 등록했습니다.

## 폼 로그인
폼 로그인을 사용하여 사용자가 해당 애플리케이션에 직접 가입하고, 로그인할 수 있도록 구현했습니다.

### 가입
가입의 경우 RestController로 요청을 받아서 해당 요청을 @ModelAttribute를 사용해 JoinDto에 담습니다. 그리고 Dto를 서비스로 전달하여 JoinService에서 UserEntity를 생성하고, UserRepository를 사용해 DB에 저장합니다.

### 로그인
실질적인 인증 과정은 `UsernamePasswordAuthentication` 필터에 캡슐화되어 있습니다. 사용자 Principal을 인증 과정에서 사용하고, 세션에 저장하기 위해 `CustomUserDetails`라는 DTO를 구현했습니다. 그리고 이를 필터로 전달하기 위한 `CustomUserDetailsService`를 구현했습니다.

또한 로그인 이후에 JWT를 생성하고 응답하기 위한 커스텀 성공 핸들러를 구현해 등록했습니다.

## OAuth2 로그인
먼저 외부 서비스로부터 client-id와 client-secret을 발급받았고, application.yml에 registration과 provider 정보를 등록했습니다.
해당 프로젝트에서는 네이버, 구글, 깃허브를 외부 서비스로 사용합니다.

실질적인 인증 과정은 `DefaultOAuth2UserService`에 캡슐화되어 있습니다. 외부 서비스로부터 받아온 사용자 정보를 정제하고 DB에 저장하기 위해 DefaultOAuth2UserService를 상속하는 `CustomOAuth2UserService`를 만들었습니다.

그리고 OAuth2Response 인터페이스를 생성하여 외부 서비스마다 리소스에 대한 attribute가 다르기 때문에 외부 서비스 각각의 구현체를 구현했습니다. OAuth2Response를 사용하여 DB에 쉽게 저장할 수 있도록 하였습니다.
또한 `CustomOAuth2User`라는 DTO를 구현했습니다.

폼 로그인과 마찬가지로 커스텀 성공 핸들러에서 JWT를 발급하고 응답하도록 구현했습니다.

## JWT 생성, 검증
JWT를 생성하고 페이로드의 정보를 파싱하기 위해 Jwts를 사용해서 JWTUtil을 구현했습니다.
또한 사용자에게 인가해주기 위해서 전송받은 JWT를 검증하고, 사용자 정보를 응답 전까지 임시적으로 저장하는 JWT Filter를 구현했습니다.

## 로그아웃
로그아웃 시 리프레시 토큰을 검증하고, DB에서 리프레시 토큰을 삭제합니다. 또한 리프레시 토큰 **쿠키의 Max-age를 0으로 설정한 후 응답하여 쿠키를 삭제**합니다.

## 재발급
access 토큰은 만료되었고, 쿠키에 refresh 토큰은 존재할 때 백엔드에서는 401번 코드를 응답하고, 해당 응답을 받은 프론트엔드는 토큰 재발급을 요청합니다.
프론트엔드로부터 재발급 요청이 오면, 쿠키로부터 refresh 토큰을 꺼내서 해당 토큰을 검증합니다. 토큰의 검증이 실패하는 경우 400번 코드를 응답하고, 검증이 성공한다면 액세스 토큰과 리프레시 토큰을 모두 재발급하여 200번 코드를 응답합니다.


# Front-end
## 로그인
폼로그인과 OAuth2의 통합 로그인 화면을 렌더링해서 보여줍니다.
**폼로그인의 경우 fetch API**를 사용하여 백엔드에 요청을 하지만, OAuth2 로그인의 경우 fetch로 외부 서비스에 요청하는 경우 CORS 에러가 발생합니다. 그 이유는 **외부 서비스에서 CORS를 허용하지 않기 때문**입니다.

그렇기 때문에 **OAuth2 로그인은 링크를 통해 요청하고** 백엔드에서 리다이렉트 하도록 했습니다. `리다이렉트는 헤더와 바디에 데이터를 담아서 전달할 수 없기 때문에 자동 전송되는 쿠키를 사용하여 토큰을 전달`합니다.

쿠키는 앞서 설명했듯 CSRF 공격의 위험성이 존재하기 때문에 액세스 토큰을 계속 담아둘 수 없습니다. `httpOnly 쿠키는 자바스크립트에서 접근이 불가능 하기 때문에 바로 로컬스토리지에 넣을 수 없습니다.` 그렇기 때문에 프론트엔드에서 다시 백엔드로 요청하여 백엔드는 httpOnly 쿠키의 값을 헤더에 넣어 전송하고, 프론트엔드는 응답받은 헤더의 액세스 토큰을 로컬 스토리지에 저장합니다.

## 로그아웃
로그아웃 시 백엔드로 요청하여 refresh 토큰을 블랙리스트 처리할 수 있도록 합니다. 요청이 성공하면 local storage의 access 토큰을 삭제하고, 루트 페이지로 리다이렉트 합니다.


## 전역 상태 공유
네비게이션 바의 링크들과 라우터들을 로그인 상태에 따라서 달리하기 위해 전역적인 상태 공유가 필요합니다. SPA에서 렌더링은 기본적으로 한 번 실행되기 때문에 로그인 상태에 따라서 렌더링을 달리할 수 있도록 처리가 필요합니다.

리액트는 props를 사용하여 컴포넌트들로 상태를 전달하는데 로그인 상태, 테마와 같이 전역적으로 사용하는 정보를 props를 사용하여 계속 전달하는 경우 `Prop Drilling`을 초래할 수 있습니다.

그렇기 때문에 전역적인 상태 공유를 위해서 Context API를 사용했습니다.
먼저 `createContext()`로 context를 생성하고, 해당 context 내부에서 공유하고자 하는 상태를 `useState()`로 생성하였습니다.

그리고 context를 사용하는 최상단 컴포넌트를 Provider로 감싸서 최상단 컴포넌트의 모든 자식 컴포넌트들이 상태를 공유할 수 있도록 하였습니다.


## 한계
폼 가입/로그인 시 사용자의 주요 정보가 http body에 담겨서 전송됩니다. http를 사용할 때는 body 데이터가 그대로 노출되고 있습니다.
그래서 데이터를 암호화 하여 통신하기 위해서는 현재 과정에서 https를 적용해야만 합니다.

또한 현재 리프레시 토큰을 DB에 저장하여 관리하고 있는데, 리프레시 토큰의 만료일마다 주기적으로 삭제해야합니다.
Redis를 사용하여 expire를 설정하거나, 배치를 적용하여 주기적으로 한꺼번에 삭제하는 과정이 필요할 것 같습니다. 