import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const Admin = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [data, setData] = useState("");

    const fetchAdmin = async () => {
        try {
            const response = await fetch("http://localhost:8080/admin", {
                method: "POST",
                credentials: "include",
                headers: {
                    "access": window.localStorage.getItem("access"), // local storage 의 access 토큰을 요청 헤더에 추가
                },
            });
    
            if (response.ok) {
                const result = await response.text();
                setData(result);
            } else {
                alert('관리자가 아닙니다.');
                // useLocation 으로 얻은 path 를 useNavigate 을 사용해 state 에 set
                navigate("/login", { state: location.pathname });
            }
        } catch (error) {
            console.log('error: ', error);
        }
    }

    fetchAdmin();
    return data && <h2>{data}</h2>;
}



export default Admin;