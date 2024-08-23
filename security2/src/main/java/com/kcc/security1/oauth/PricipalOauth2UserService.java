package com.kcc.security1.oauth;

import com.kcc.security1.auth.PrincipalDetail;
import com.kcc.security1.model.User;
import com.kcc.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
// OAuth를 이용하기 위한 Security - (PricipalOauth2UserService = UserDetailService)
// pricipalOauth2UserService통해서 OAuth2 인증 요청을 처리하여 사용자 정보를 담은 OAuth2User 객체를 반환하는 메서드
public class PricipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    // 인증 요청에 대한 정보를 출력하고 부모 클래스의 loadUser 메서드를 호출하여 실제 인증 프로세스를 수행
    // 로그인하면 자연스럽게 loadUser 호출 - OAuth2UserRequest userRequest로 정보를 가져올수 있다.
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //System.out.println("userRequest = " + userRequest);  // 인증 요청에 대한 정보를 출력
        //System.out.println("getClientRegistation:" + userRequest.getClientRegistration());  // 클라이언트 등록 정보를 출력
        //System.out.println("getAccessToken:" + userRequest.getAccessToken());  // 액세스 토큰을 출력
        //System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());  // OAuth2User 객체의 속성을 출력

        // oAuth2User정보를 얻어옴.
        OAuth2User oAuth2User = super.loadUser(userRequest);  // 부모 클래스인 DefaultOAuth2UserService의 loadUser 메서드를 호출하여 기본적인 OAuth2User 사용자 정보 객체를 불러옴.
        System.out.println("getAttributes: " + oAuth2User.getAttributes());
        // 구글로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료(사후처리) -> Access Token 요청
        //userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필 추출 -> 회원가입 -> OAuth2User -> Athentication 객체로 만듦

        // OAuth2User 객체의 속성을 사용한 회원가입 처리하여 Athentication(인증,권한 유지) 객체로 만듦.
        String provider = userRequest.getClientRegistration().getClientId(); // google(소셜 로그인 제공자)
        String providerId = oAuth2User.getAttribute("sub");  // 숫자(소셜 로그인 제공자에서 부여한 고유 ID)
        String username = provider + "_" + providerId;  // 사용자의 고유 ID(google_숫자) 형식으로 생성
        String email = oAuth2User.getAttribute("email"); // email - 사용자 이메일 (OAuth2 정보에서 추출)
        String role = "ROLE_USER"; // 사용자 권한 (여기서는 기본적으로 "ROLE_USER" 설정)

        // oAuth2User정보를 바탕으로 User 객체를 만듦
        User userEntity = userRepository.findByUsername(username); // 회원가입이 되어 있는지 확인(있으면, Athentication만 만들어주면 됨)

        if(userEntity == null) {  // 회원가입이 되어있지 않으면 회원가입을 시켜줌(즉, userEntity == null이면)
            // 회원가입
            userEntity = User.builder()
                    .username(username)
                    .password("1234")
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);  // 해당 정보를 기반으로 데이터베이스에 사용자 정보를 저장
        }

        // PrincipalDetail를 통해 Athentication만들어줌.
        return new PrincipalDetail(userEntity, oAuth2User.getAttributes()); // OAuth2 로그인을 통해 가져온 사용자 정보를 담은 PrincipalDetail 객체를 생성하고 반환(즉, 이 객체는 이후 인증 처리 및 권한 부여에 사용될 수 있음.)
    }
}
