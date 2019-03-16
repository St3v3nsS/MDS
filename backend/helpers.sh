#!/bin/sh

# Register curl
curl -H "username:bogdan" -H "password:ceva" -v localhost:3000/register

# Login curl
curl -H "username:bogdan" -H "password:altceva" -v localhost:3000/login
