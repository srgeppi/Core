import { useEffect, useState } from 'react';

export default function Home() {
  const [apiStatus, setApiStatus] = useState<string>('Loading...');

  useEffect(() => {
    // Use Next.js API proxy (defined in next.config.js)
    // This proxies /api/* to the FastAPI backend
    fetch('/api/health')
      .then(res => res.json())
      .then(data => setApiStatus(data.status))
      .catch((err) => {
        console.error('API Error:', err);
        setApiStatus('Error connecting to API');
      });
  }, []);

  return (
    <div style={{ padding: '2rem' }}>
      <h1>Minecraft Server Dashboard</h1>
      <p>API Status: {apiStatus}</p>
      <p>Welcome! This is your Next.js frontend connected to FastAPI.</p>
    </div>
  );
}

