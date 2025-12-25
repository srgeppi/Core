/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // For Docker networking
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://api:8000/:path*',
      },
    ];
  },
};

module.exports = nextConfig;

