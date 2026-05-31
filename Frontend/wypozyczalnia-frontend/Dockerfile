FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

EXPOSE 5173
# Uruchomienie deweloperskie z obsługą sieci zewnętrznej (--host)
CMD ["npm", "run", "dev", "--", "--host"]