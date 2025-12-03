import http from "k6/http";
import {check, sleep} from "k6";

export const options = {
    vus: 10,              // 동시에 접속하는 사용자 10명
    duration: "60s",       // 60초 동안 부하

    // thresholds: {
    //     http_req_duration: ["p(95)<400"], // p95 400ms 이하 목표
    //     http_req_failed: ["rate<0.01"],   // 오류율 1% 미만
    // },
};

export default function () {
    const BASE_URL = "http://localhost:8080/api/v1/goods";

    // 랜덤 상품 ID 조회 (1~1000)
    const goodsId = Math.floor(Math.random() * 1000) + 1;

    const res = http.get(`${BASE_URL}/${goodsId}`);
    console.log("status", res.status);

    check(res, {
        "status is 200": (r) => r.status === 200,
        // "latency < 300ms": (r) => r.timings.duration < 300,
    });

    sleep(1); // 1초 대기 (실사용자 환경을 흉내냄)
}
