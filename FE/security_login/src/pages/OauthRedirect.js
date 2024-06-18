import { useSearchParams } from "react-router-dom";

const OAuth2Redirect = () => {
    // local storage username set
    const [queryParams, setQueryParms] = useSearchParams();
    const username = queryParams.get('username');
    window.localStorage.setItem("username", username);
    // request access token in header using httpOnly cookie, and set access token to local storage
    OAuth2JwtHeaderFetch();
    window.location.href="/";
    return;
};

const OAuth2JwtHeaderFetch = async () => {
    try {
        const response = await fetch("http://localhost:8080/oauth2-jwt-header", {
            method: "POST",
            credentials: "include",
        });

        if(response.ok){
            window.localStorage.setItem("access", response.headers.get("access"));
        } else {
            console.log("access header request failed");
        }
    } catch (error) {
        console.log("error: ", error);
    }
}

export default OAuth2Redirect;