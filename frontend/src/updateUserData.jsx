import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    MDBContainer,
    MDBInput,
    MDBCheckbox
} from 'mdb-react-ui-kit';
import { useParams } from 'react-router-dom';
import './css/updateUserData.css';

function UpdateUser() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('');
    const [deleteavatar, setDeleteavatar] = useState(false);
    const [avatar, setPhoto] = useState(null);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const { id } = useParams();

    const getData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/getinfoUser/${id}`, {
                withCredentials: true,
                headers: {
                    "Content-Type": "application/json"
                }
            });
            setUsername(response.data.username);
            setEmail(response.data.email);
            setPassword(response.data.password);
            setRole(response.data.role);
        } catch (error) {
            setError("Не удалось получить данные");
        }
    };

    const handleUpdate = async () => {
        setError('');
        setSuccess('');
    
        const formData = new FormData();
        formData.append('username', username);
        formData.append('email', email);
        formData.append('password', password);
        formData.append('role', role);
        formData.append('deleteAvatar', deleteavatar); // Название должно соответствовать серверному @RequestParam
        if (avatar) {
            formData.append('avatar', avatar); // Передаем файл только если он выбран
        }
    
        try {
            const response = await axios.put(`http://localhost:8080/updateUser/${id}`, formData, {
                withCredentials: true,
                headers: {
                    "Content-Type": "multipart/form-data", // Обязателен для FormData
                },
            });
            setSuccess('Данные успешно обновлены');
        } catch (error) {
            console.error('Ошибка запроса:', error.response || error.message);
            setError(error.response?.data?.message || 'Ошибка при обновлении данных');
        }
        getData();
    };

    useEffect(() => {
        getData();
    }, []);

    return (
        <div className="d-flex justify-content-center align-items-center">
            <div style={{ width: '600px', height: '550px' }}>
                <MDBContainer className="p-3">
                    <h2 className="mb-4 text-center">Обновление данных</h2>
                    {error && <p className="text-danger">{error}</p>}
                    {success && <p className="text-success">{success}</p>}
                    {error === "" && (
                        <>
                            <p>Имя пользователя</p>
                            <MDBInput
                                wrapperClass="mb-3"
                                id="fullName"
                                placeholder="Имя пользователя"
                                value={username}
                                type="text"
                                onChange={(e) => setUsername(e.target.value)}
                            />
                            <p>Пароль</p>
                            <MDBInput
                                wrapperClass="mb-3"
                                placeholder="Пароль"
                                id="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />

                            <p>Аватар</p>
                            <MDBInput
                                wrapperClass="mb-3"
                                type="file"
                                accept="image/*"
                                id="avatar"
                                onChange={(e) => setPhoto(e.target.files[0])}
                            />
                            <MDBCheckbox
                                wrapperClass="mb-3"
                                id="deleteavatarcheckbox"
                                label="Удалить аватар"
                                checked={deleteavatar}
                                onChange={(e) => setDeleteavatar(e.target.checked)}
                            />
                            <button
                                className="mb-4 d-block mx-auto fixed-action-btn btn-primary"
                                style={{ height: '40px', width: '100%' }}
                                onClick={handleUpdate}
                            >
                                Сохранить
                            </button>
                        </>
                    )}
                </MDBContainer>
            </div>
        </div>
    );
}

export default UpdateUser;