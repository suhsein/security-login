import { Cookies } from "react-cookie";

// access 토큰은 만료되었고, 쿠키에 refresh 토큰은 존재할 때
// access 토큰 만료 -> 백엔드에서 401 응답 -> 프론트에서 토큰 재발급 요청
const fetchReissue = async () => {
    try {
        const response = await fetch("http://localhost:8080/reissue", {
            method: "POST",
            credentials: "include",
        });

        if (response.ok) { // 토큰 재발급 성공
            window.localStorage.setItem('access', response.headers.get("access"));
            return true;
        } else { // 토큰 재발급 실패
            window.localStorage.removeItem("access");
            const cookies = new Cookies();
            cookies.set("refresh", null, { maxAge: 0 });
        }
    } catch (error) {
        console.log("error: ", error);
    }
    return false;
}

export default fetchReissue;