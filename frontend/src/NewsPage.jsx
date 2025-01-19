import React, { useState, useEffect } from 'react';
import NewsList from './NewsList';
import axios from 'axios';
import { Link } from 'react-router-dom';


function NewsPage() {
  const [news, setNews] = useState([]);
  const [role, setRole] = useState('');

  const fetchUserRole = async () => {
    try {
        const response = await axios.get('http://localhost:8080/dashboard', {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
        setRole(response.data.role);
    } catch (error) {
        console.error(error);
    }
};
      useEffect(() => {
        const fetchNews = async () => {
          try {
            const response = await axios.get('http://localhost:8080/api/news');
            setNews(response.data);
          } catch (error) {
            console.error('Error fetching news:', error);
          }
        };
        fetchNews();
        fetchUserRole();
    }, []);

  const handleDeleteNews = async (id) => {
      try {
        await axios.delete(`http://localhost:8080/api/news/${id}`, {
        withCredentials: true,
            headers: {
                "Content-Type": "application/json"
            }
        });
          setNews(news.filter((item) => item.id !== id));
        } catch (error) {
          console.error('Error deleting news:', error);
        }
      };

      const handleEditNews = () => {
        }
      

        return (
                <div>
                  <div className='addNews'>
                    <h1>Новости</h1>
                    {
                      role === "MODER" ? (
                        <Link to="/news/add"><button>+</button></Link>
                      ) : (
                        null
                      )
                    }
                  </div>
                <NewsList news={news} onDeleteNews={handleDeleteNews} onEditNews={handleEditNews} role={role} />
                </div>
      );
  }

export default NewsPage;