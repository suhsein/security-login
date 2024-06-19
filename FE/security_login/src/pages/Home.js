import { useLogin } from "../contexts/AuthContext";

const Home = () => {
    const { isLoggedIn, loginUser } = useLogin();

    return (
        <div>
            <h1>Home</h1>
            {isLoggedIn && <span>{loginUser}님 환영합니다.</span>}
        </div>
    );
};

export default Home;