// todo: write login router


function login (req, res) {
    console.log(req.headers)
    let username = req.get("username");
    let password = req.get("password");

    if (username == undefined || password == undefined) {
        res.status(400).end();
    } else {
        app.db.collection("users").find({'username': username, 'password' : password}).toArray(function(err, docs) {
            if (err != null) {
                console.log(err);
            }

            if (docs.length == 0) {
                res.status(400).end();
            } else if (docs.length == 1) {
                res.status(200).end();
            }
        });
    }
}