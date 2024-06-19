import { useState } from "react";

const Home = () => {
    const [username, setUsername] = useState(localStorage.getItem("username"));
    const [flag, setFlag] = useState(typeof username !== 'undefined' && username !== null);

    return (
        <div>
            <h1>Home</h1>
            {flag && <span>{username}님 환영합니다.</span>}
        </div>
    );
};

export default Home;