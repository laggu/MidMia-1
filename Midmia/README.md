# MidMia(미드미아) - Android
<hr />

## 개요
>+ MidMia(미드미아)는 스마트폰(어른)과 웨어러블 디바이스(아이)간의 Bluetooth 4.0 통신을 이용하여 웨어러블 디바이스(아이)의 위치를 찾는 프로젝트입니다.
>+ 미아방지를 위한 IoT 장비로 개발되었으며, 스마트폰에서 Bluetooth통신을 통해 아이의 위치를 수신하여 Google Map에 보여주어 웨어러블 디바이스의 위치를 찾을 수 있습니다.
>+ 뿐만 아니라, 아이의 안전범위를 설정하여, 범위 밖으로 나가거나 Bluetooth 연결이 끊기게 되면 스마트폰(부모)로 알림이 갈 수 있도록 제작되었습니다.

## 시작하기
<hr/>

>MidMia(미드미아)는 하나의 스마트폰에서 여러 디바이스를 관리할 수 있도록 만들었습니다.

### 전제조건
>MidMia가 설치된 스마트폰과 통신 가능한 Wearable Device가 필요합니다.

### 설치
>1. Google Play Store 접속
>2. '미드미아'을 입력
>3. 어플리케이션 다운로드
>4. 설치된 어플리케이션 실행


## Running test
>__1. 아이정보 등록__
>>+ 'Main'메뉴에서 아이 정보 등록.
>>+ 아이의 이름, 성별, 나이, 사진, 통신주기 등을 설정.
>>+ '기기연결'을 통해 Wearable Device와의 블루투스 통신 확인 후, 연결.
>>+ 등록된 아이정보는 내부 DB에 고스란히 저장.
>>+ 내부 DB에 저장된 아이 정보는 어플리케이션 실행 시, 정보를 가져온다.

>__2. 아이정보 수정 및 삭제__
>>+ 'Setting'메뉴에서 아이 정보를 수정 및 삭제.
>>+ Press 방식을 이용하여, 일정 시간 누를 시, 수정 및 삭제 가능.
>>+ 수정된 아이 정보는 내부 DB에 저장.

>__3. 아이위치 파악__
>>+ 'Map'메뉴에서 아이 정보 등록.
>>+ 사용자 위치 및 블루투스 통신중인 Device의 위치 확인 가능.
>>+ 다중 Thread를 이용하여, 여러대의 Device와 블루투스 통신 가능.

>__4. (수동)위험거리 및 통신 주기 설정__
>>+ 'Setting'메뉴에서 아이의 위험거리 및 통신 주기 설정.
>>+ 통일된 정보가 아닌, 아이(Device)마다 위험 거리 및 통신 주기 설정 가능.
>>+ 최대 50m, 1분까지 설정 가능.
>>+ 설정된 위험거리를 벗어났을 시, 부모에게 알려줌.

>__5. (자동)위험거리 및 통신 주기 설정__
>>+ 'Map'에 표시된 미아 위험지역에 진입 시, 자동으로 10m, 1초로 설정된다.

>__6. 미아 위험지역 확인__
>>+ 'Map'메뉴에 붉은색으로 미아 위험지역 구성.
>>+ 미아 위험지역 위치 정보를 Update하여 Map에 Marking.
>>+ 미아 위험지역에 진입 시, 부모에게 알림.
>>+ 'Main'메뉴에 위험지역 표시 활성화.

>__7. 미아 정보 서버 전달__
>>+ 미아 발생 시(블루투스 통신 끊김, 안전거리 벗어남), 부모에게 Dialog창으로 미아 여부 확인 후, 미아로 판별되면 마지막 아이 위치를 map에 보여주고 서버로 아이 정보 송신.
>>+ 송신된 정보는 차후, 새로운 미아 위험지역 구성 데이터로 사용.

## 개발 언어 및 툴
>+ 언어 : Java
>+ 개발 툴 : Android Studio
>+ 라이브러리 : Google Map API, Bluetooth 4.0

## 버전관리
>우리는 버전관리를 위해 GitHub를 사용하였으며, 확인하시려면 repository의 tag를 보세요.

## ChangeLog
> ### 2017.05
>    ```
>    + HW / SW system Architecture clear.
>    + 안드로이드 <-> 서버 통신 성공.
>    + 안드로이드 <-> 아두이노 통신 성공.
>    + Google Map 연동 성공.
>    ```
> ### 2017.06
>    ```
>    + Class 설계 완료.
>    + 내부 DB 연동 완료.
>    + 아두이노와 블루투스 연결 처리.(Background Thread 처리).
>    + 아두이노에서 받은 위치 정보를 Google Map에 띄움.
>    ```
> ### 2017.07
>    ```
>    + 서버에 미아 정보 송신.
>    + UI - 아두이노 방향으로 화살표 이동 처리.
>    + 2대 이상의 아두이노와 동시에 블루투스 연결 처리.
>    + 아이 정보 추가 및 삭제 테스트 - 완료.
>    ```
> ### 2017.08
>    ```
>    + 미아 위험 지역 데이터를 Google Map에 Marking.
>    + 위험지역 진입 및 나올 시, 아이 위험 반경 조절 확인.
>    + 기타 여러 기능 테스트 - 완료.
>    + 최종 테스트 - 완료.
>    ```
> ### 2018.03.26
>    ```
>    + README Update.
>    ```

## THANKS
>+ 남송휘
>+ 최용주
>+ 구글링

## BUGS
> 버그가 발견되면 해당 버그 현상을 cru6548@gmail.com 리포트해주시면 됩니다.

## 저자
>박예훈 - Beyond_Imagination-고려대학교(세종)-https://github.com/gangjung

## License
> Copyright (c) 2017, Beyond_Imagination
> This project is licenced under a Creative Commons license: http://creativecommons.org/licenses/by/2.5/
