import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import basicSsl from '@vitejs/plugin-basic-ssl' 

export default defineConfig({
  plugins: [
    react(),
    basicSsl() //dodanie wtyczki ssl
  ],
  server: {
    https: true // wymusi HTTPS
  }
})