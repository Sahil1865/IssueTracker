import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../api/authApi";
import useAuth from "../context/useAuth";
import { jwtDecode } from "jwt-decode";


const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
  const res = await loginUser({ email, password });
  console.log("RESPONSE:", res);

  const token = res.data.token;
  console.log("TOKEN:", token);

  const decoded = jwtDecode(token);
  console.log("DECODED:", decoded);

  login({
    token,
    role: decoded.role,
    email: decoded.sub
  });

  navigate("/dashboard");

} catch (err) {
  console.error("ERROR CAUGHT:", err);
  setError("Invalid email or password");
}

  };

  return (
    <div className="auth-container">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h2 style={{ textAlign: "center" }}>Login</h2>

        {error && <p style={{ color: "#f87171" }}>{error}</p>}

        <input
          className="auth-input"
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          className="auth-input"
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button className="auth-button">Login</button>

        <p className="auth-link-text">
          Don’t have an account?{" "}
          <Link to="/register" className="auth-link">
            Register
          </Link>
        </p>
      </form>
    </div>
  );
};

export default Login;
