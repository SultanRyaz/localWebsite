import React from 'react';
import NewsItem from './NewsItem';

const NewsList = ({ news, onDeleteNews, role}) => {
    return (
        <div>
            {news.map(item => (
                <NewsItem key={item.id} news={item} onDelete={onDeleteNews} role={role}/>
            ))}
        </div>
    );
};

export default NewsList;