FROM node:10.15.3-alpine

# Create app directory
RUN mkdir -p /srv/app
WORKDIR /srv/app

COPY package.json .

# Install app dependiences
RUN npm install --no-bin-links --silent --no-optional

ENV NODE_ENV 'development'

# Copy sources
COPY . /srv/app

EXPOSE 3000

RUN date +"%Y%m%d%H%M%S" > /srv/build.txt
CMD ["npm", "start"]