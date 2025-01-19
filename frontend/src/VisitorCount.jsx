import axios from 'axios';
import React, { useEffect, useState } from 'react';

function VisitorCount() {
    const [visitorCount, setVisitorCount] = useState(0);  // Начальное значение - 0
    const [visitorError, setVisitorError] = useState('');

    useEffect(() => {
        console.log("useEffect вызван");
        const registerVisitor = async () => {
            try {
                const response = await axios.get("http://localhost:8080/incrementvisitor", {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
                setVisitorCount(response.data);
            } catch (error) {
                console.error("Ошибка при регистрации посетителя", error);
                setVisitorError("Ошибка при регистрации посетителя");
            }
        };
        registerVisitor();
    }, []);
    
    return (
        <div>
            {visitorError && <div>{visitorError}</div>}
            <div>{`Количество посещений: ${visitorCount}`}</div>
        </div>
    );
}

export default VisitorCount;