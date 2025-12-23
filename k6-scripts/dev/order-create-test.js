import http from "k6/http";
import {check} from "k6";

export const options = {
    vus: 15,              // 동시에 접속하는 사용자 수
    duration: "30s",       // 테스트 실행 시간
    thresholds: {
        // http_req_duration: ["p(95)<400"], // p95 400ms 이하 목표
        http_req_failed: ["rate<0.01"],   // 오류율 1% 미만
    },
};

// 초기 설정: 로그인 및 토큰 획득
export function setup() {
    const uniqueId = Date.now();
    const k6TestUser = {
        name: `k6test_${uniqueId}`,
        email: `k6test_${uniqueId}@test.com`,
        password: "k6test"
    };

    // 테스트용 사용자 회원가입
    http.post("http://localhost:8080/api/v1/users/signup", JSON.stringify(k6TestUser), {
        headers: {
            "Content-Type": "application/json",
        }
    });

    // 로그인 요청 및 토큰 획득
    const loginRes = http.post("http://localhost:8080/api/v1/users/login", JSON.stringify({
        email: k6TestUser.email,
        password: k6TestUser.password
    }), {
        headers: {"Content-Type": "application/json"}
    });

    check(loginRes, {
        "login status is 200": (r) => r.status === 200
    });

    const accessToken = loginRes.json("data.accessToken");
    const refreshToken = loginRes.json("data.refreshToken");

    return {accessToken, refreshToken};
}

// 상품 조회 테스트
export default function (data) {
    const randomGoodsId = Math.floor(Math.random() * 10000) + 1;
    const url = `http://localhost:8080/api/v1/order/${randomGoodsId}`;

    const payload = JSON.stringify({
        address: "서울",
        orderGoodsQuantity: 1
    });

    const params = {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${data.accessToken}`,
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        "status is 201": (r) => r.status === 201,
        // "latency < 300ms": (r) => r.timings.duration < 300,
    });
}
