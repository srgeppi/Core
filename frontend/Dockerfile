FROM node:20-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy application code
COPY . .

# Expose port
EXPOSE 3000

# Start Next.js dev server (for development)
# For production, you'd build and serve static files
CMD ["npm", "run", "dev"]

