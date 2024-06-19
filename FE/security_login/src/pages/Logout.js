import { Cookies } from "react-cookie";
import { useNavigate } from "react-router-dom";

const Logout = () => {
    const navigate = useNavigate();
    const fetchLogout = async () => {
        try {
            // 로그아웃 요청 시 백엔드에서 refresh token 블랙리스트 처리
            const response = fetch("http://localhost:8080/logout", {
                method: "POST",
                credentials: "include",
            });
            if(response.ok){
                alert("logout successful");
                // access token 삭제 (로컬 스토리지)
                window.localStorage.setItem("access", null);
                // refresh token 삭제 (쿠키)
                const cookies = new Cookies();
                cookies.set("refresh", 0, { maxAge: 0 });
            } else{
                const message = await response.text();
                alert(message);
            }
            navigate("/", {replace : true});
        } catch (error) {
            console.log("error: ", error);
        }
    }
    fetchLogout();
    return;
}

export default Logout;