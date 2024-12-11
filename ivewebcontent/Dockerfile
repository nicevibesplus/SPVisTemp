FROM node:alpine

##### used git update-index --assume-unchanged to untrack file; same for docker-compose.yml #####
# Create app directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied where available (npm@5+)
COPY package*.json ./

RUN npm config set https-proxy http://wwwproxy.uni-muenster.de:3128
RUN npm config set proxy http://wwwproxy.uni-muenster.de:3128
RUN npm install
# If you are building your code for production
# RUN npm ci --omit=dev

# Bundle app source
COPY . .

#EXPOSE 2000
CMD [ "node", "server.js" ]
