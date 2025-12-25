# Stage 1: Build the plugin
FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

# Stage 2: Run Minecraft server
FROM eclipse-temurin:21-jre-alpine

WORKDIR /minecraft

# Install curl
RUN apk add --no-cache curl

# Download PaperMC server (only if paper.jar doesn't exist in mounted volume)
# The server folder is mounted, so we'll use the existing paper.jar if it exists
# Otherwise, download it
RUN if [ ! -f /minecraft/paper.jar ]; then \
    curl -o /minecraft/paper.jar https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/latest/downloads/paper-1.21.1-latest.jar; \
    fi

# Copy plugin JAR from build stage to plugins folder
COPY --from=build /app/build/libs/HelloPlugin-*.jar /tmp/HelloPlugin.jar

# Create entrypoint script that copies plugin and starts server
RUN echo '#!/bin/sh' > /entrypoint.sh && \
    echo 'cp /tmp/HelloPlugin.jar /minecraft/plugins/ 2>/dev/null || true' >> /entrypoint.sh && \
    echo 'if [ ! -f /minecraft/eula.txt ]; then echo "eula=true" > /minecraft/eula.txt; fi' >> /entrypoint.sh && \
    echo 'exec java -Xms${MEMORY:-2G} -Xmx${MEMORY:-2G} -jar /minecraft/paper.jar --nogui' >> /entrypoint.sh && \
    chmod +x /entrypoint.sh

# Expose Minecraft port
EXPOSE 25565

# Set memory limits
ENV MEMORY=2G

# Start command
ENTRYPOINT ["/entrypoint.sh"]
