import http from "k6/http";
import {check} from "k6";

export const options = {
    vus: 15,                // 동시에 접속하는 가상 사용자 수 (15명)
    duration: "30s",        // 30초 동안 테스트 진행
    thresholds: {
        http_req_failed: ["rate<0.01"],   // 오류율 1% 미만 목표
    },
};

// 1. 테스트 시작 전 딱 한 번 실행: 15명의 유저를 가입시키고 토큰을 따둡니다.
export function setup() {
    const tokens = [];
    const password = "k6test_password";

    for (let i = 1; i <= 15; i++) {
        const userEmail = `k6test_user_${i}@test.com`;
        const userName = `k6test_user_${i}`;

        // 테스트용 사용자 회원가입 (이미 존재하면 서버 로직에 따라 무시되거나 에러가 날 수 있음)
        http.post("http://localhost:8080/api/v1/users/signup", JSON.stringify({
            name: userName,
            email: userEmail,
            password: password
        }), {
            headers: {"Content-Type": "application/json"}
        });

        // 로그인 요청 및 토큰 획득
        const loginRes = http.post("http://localhost:8080/api/v1/users/login", JSON.stringify({
            email: userEmail,
            password: password
        }), {
            headers: {"Content-Type": "application/json"}
        });

        // 응답에서 토큰 추출
        const accessToken = loginRes.json("data.accessToken");
        if (accessToken) {
            tokens.push(accessToken);
        }
    }

    // 15명의 토큰 배열을 default 함수로 전달
    return {tokens: tokens};
}

// 2. 실제 30초 동안 반복 실행되는 주문 테스트
export default function (data) {
    // __VU (1~15)를 사용해 각 가상 사용자가 자신만의 토큰을 선택하도록 함
    const myToken = data.tokens[__VU - 1];

    const randomGoodsId = Math.floor(Math.random() * 10000) + 1;
    const url = `http://localhost:8080/api/v1/order/${randomGoodsId}`;

    const payload = JSON.stringify({
        address: "서울",
        orderGoodsQuantity: 1
    });

    const params = {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${myToken}`,
        },
    };

    // 주문 API 호출
    const res = http.post(url, payload, params);

    // 결과 검증 (오직 주문 API의 성공 여부만 체크)
    check(res, {
        "status is 201": (r) => r.status === 201,
    });
}