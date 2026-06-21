# ==========================================
# Stage 1: Build the Angular Application
# ==========================================
FROM node:22-alpine AS builder
WORKDIR /build

COPY frontend/package.json frontend/package-lock.json frontend/.npmrc ./
RUN npm ci

COPY frontend/ ./
RUN npx ng build --configuration production

# ==========================================
# Stage 2: Serve with Nginx
# ==========================================
FROM nginx:1.27-alpine
COPY --from=builder /build/dist/frontend/browser /usr/share/nginx/html
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
