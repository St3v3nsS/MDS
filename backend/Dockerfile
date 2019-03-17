FROM node:10.15.3-alpine

EXPOSE 3000

# Create app directory
RUN mkdir -p /srv/app
WORKDIR /srv/app

COPY package.json .

# Install app dependiences
RUN npm install --no-bin-links --silent --no-optional

ENV NODE_ENV 'development'

# Copy sources
COPY . /srv/app

RUN date +"%Y%m%d%H%M%S" > /srv/build.txt

CMD ["node", "server.js"]