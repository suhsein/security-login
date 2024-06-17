import { useState } from "react";

const JoinForm = () => {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const joinHandler = async (e) => {
        e.preventDefault();
        const credentials = { username, password };
        fetchJoin(credentials);
    }
    return (
        <div>
            <h1>Join</h1>
            <form onSubmit={joinHandler}>
                <p>Username : <input type="text" name="username" value={username} placeholder="username" onChange={(e) => setUsername(e.target.value)} /></p>
                <p>Password : <input type="password" autoComplete="off" name="password" placeholder="password" onChange={(e) => setPassword(e.target.value)} /></p>
                <input type="submit" value="Join" />
            </form>
        </div>
    );
}

const fetchJoin = async (credentials) => {
    try {
        const response = await fetch("http://localhost:8080/join", {
            method: "POST",
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(credentials),
        });
        if (response.ok) {
            alert("Join Successful");
            window.location.href="/login";
        } else {
            alert("Join Failed");
        }
    } catch (error) {
        console.log("Error: ", error);
    }

}

export default JoinForm;