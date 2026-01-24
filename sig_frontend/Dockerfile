# Build stage
FROM node:18-alpine as builder
WORKDIR /usr/src/app

# Copy package files
COPY package.json ./

# Install dependencies (using npm to support 'overrides', with legacy peer deps for legacy packages)
RUN npm install --legacy-peer-deps

# Copy application code
COPY . .

# Build for production (Nuxt.js SPA mode generates static files in dist/)
ENV NODE_ENV=production
ENV NODE_OPTIONS=--openssl-legacy-provider
RUN npm run generate


# Production stage with nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx static assets
RUN rm -rf ./*

# Copy built application from builder (Nuxt SPA generates to dist/ folder)
# Set ownership to nginx user
COPY --from=builder --chown=nginx:nginx /usr/src/app/dist /usr/share/nginx/html

# Create nginx configuration
# Listen on 8080 for non-root usage
RUN echo 'server { \
    listen 8080; \
    server_name localhost; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { \
    try_files $uri $uri/ /index.html; \
    } \
    location /api { \
    proxy_pass http://backend:8080; \
    proxy_http_version 1.1; \
    proxy_set_header Upgrade $http_upgrade; \
    proxy_set_header Connection "upgrade"; \
    proxy_set_header Host $host; \
    proxy_cache_bypass $http_upgrade; \
    } \
    gzip on; \
    gzip_vary on; \
    gzip_min_length 1024; \
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/json application/javascript; \
    }' > /etc/nginx/conf.d/default.conf

# Permissions for nginx user to write to cache/pid locations if needed
# (nginx:alpine default config uses /var/cache/nginx and /var/run/nginx.pid)
RUN chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    chown -R nginx:nginx /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

# Switch to non-root user
USER nginx

EXPOSE 8080

# Add healthcheck (adjust port to 8080)
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080 || exit 1

CMD ["nginx", "-g", "daemon off;"]
