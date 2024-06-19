import { useNavigate, useSearchParams } from "react-router-dom";

const OAuth2Redirect = () => {
    const navigate = useNavigate();

    const OAuth2JwtHeaderFetch = async () => {
        const [queryParams, setQueryParms] = useSearchParams();
        try {
            const response = await fetch("http://localhost:8080/oauth2-jwt-header", {
                method: "POST",
                credentials: "include",
            });

            if (response.ok) {
                // local storage access token set
                window.localStorage.setItem("access", response.headers.get("access"));
                // local storage username set
                const username = queryParams.get('username');
                window.localStorage.setItem("username", username);
            } else {
                alert('접근할 수 없는 페이지입니다.');
            }
            navigate('/', { replace: true });
        } catch (error) {
            console.log("error: ", error);
        }
    }

    // request access token in header using httpOnly cookie, and set access token to local storage
    OAuth2JwtHeaderFetch();
    return;
};


export default OAuth2Redirect;