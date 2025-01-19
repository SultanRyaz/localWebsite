import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import {
    MDBContainer,
    MDBInput,
    MDBBtn,
} from 'mdb-react-ui-kit';
import './css/SignUpPage.css';

function SignupPage() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [avatar, setPhoto] = useState(null);
    const history = useNavigate();

    const handleSignup = async () => {
        try {
            if (!username || !email || !password || !confirmPassword) {
                setError('Пожалуйста, заполните все поля.');
                return;
            }

            if (password !== confirmPassword) {
                throw new Error("Пароли не совпадают");
            }

            const formData = new FormData();
            formData.append('username', username);
            formData.append('password', password);
            formData.append('email', email);
            if(avatar){
                formData.append('avatar', avatar);
            }
            const response = await axios.post('http://localhost:8080/register', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            console.log(response.data);
            history('/login', {state:{message: "Вы успешно зарегистрировались!"}});
        } catch (error) {
            console.error('Signup failed:', error.response ? error.response.data : error.message);
            setError(error.response ? error.response.data : error.message);
        }
    };

    return (
        <div className="d-flex justify-content-center align-items-center vh-100">
            <div className="border rounded-lg p-4" style={{ width: '600px', height: 'auto' }}>
                <MDBContainer className="p-3">
                    <h2 className="mb-4 text-center">Регистрация</h2>
                    {error && <p className="text-danger">{error}</p>}
                    <MDBInput wrapperClass='mb-3' id='fullName' placeholder={"Имя пользователя"} value={username} type='text'
                              onChange={(e) => setUsername(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' placeholder='Адрес электронной почты' id='email' value={email} type='email'
                              onChange={(e) => setEmail(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' placeholder='Пароль' id='password' type='password' value={password}
                              onChange={(e) => setPassword(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' placeholder='Повторите пароль' id='confirmPassword' type='password'
                              value={confirmPassword}
                              onChange={(e) => setConfirmPassword(e.target.value)} />
                    <MDBInput wrapperClass='mb-3' type='file' accept='image/*' id='avatar' onChange={(e) => setPhoto(e.target.files[0])} />
                    
                    <button className="mb-4 d-block mx-auto fixed-action-btn btn-primary"
                            style={{ height: '40px', width: '100%' }}
                            onClick={handleSignup}>Зарегистрироваться   
                    </button>

                    <div className="text-center">
                        <p>Уже зарегистрированы? <a href="/login">Войти</a></p>
                    </div>

                </MDBContainer>
                
            </div>
        </div>
    );
}

export default SignupPage;