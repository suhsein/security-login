import { Link } from 'react-router-dom';
import { useLogin } from '../contexts/AuthContext';

const NavBar = () => {
    const { isLoggedIn } = useLogin();
    return (
        <nav>
            <Link to="/">Home</Link>
            {!isLoggedIn && <Link to="/join">Join</Link>}
            {!isLoggedIn && <Link to="/login">Login</Link>}
            {isLoggedIn && <Link to="/logout">Logout</Link>}
        </nav>
    );
}

export default NavBar;
