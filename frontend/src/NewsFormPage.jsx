import React, { useState, useEffect } from 'react';
import NewsForm from './NewsForm';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

function NewsFormPage() {
  const navigate = useNavigate();
  const { id } = useParams(); // Получаем ID из URL, если есть
  const [newsToEdit, setNewsToEdit] = useState(null);
  const [userId, setUserId] = useState('');

  useEffect(() => {
  const fetchUserId = async () => {
    try {
        const response = await axios.get('http://localhost:8080/dashboard', {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        setUserId(response.data.id);
    } catch (error) {
        setError('Не удалось загрузить информацию о пользователе');
        console.error(error);
    }
  }
    
    const fetchNewsToEdit = async () => {
       if (id) {
         try {
           const response = await axios.get(`http://localhost:8080/api/news/${id}`, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
           setNewsToEdit(response.data);
         } catch (error) {
             console.error('Error fetching news item for edit:', error);
             navigate('/news');
           }
         }
      };
      fetchUserId();
    fetchNewsToEdit();
  }, [id, navigate]);

  

  const handleAddOrUpdateNews = async (newsItem) => {
       navigate('/news');
  };


  return (
    <div>
      <h1>{id ? "Редактирование новости" : "Создание новости"}</h1>
        <NewsForm  onNewsAdded={handleAddOrUpdateNews} selectedNews={newsToEdit} onNewsUpdated={handleAddOrUpdateNews} userId={userId}/>
    </div>
  );
}

export default NewsFormPage;