<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <script src="https://unpkg.com/htmx.org@2.0.4"
            integrity="sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+"
            crossorigin="anonymous"></script>
    <title>Lobby</title>
    <style>
        section {
            background: #ebfbff;
            box-shadow: 10px 5px 5px gray;
            margin-bottom: 20px;
            padding: 10px;
        }
    </style>
</head>

<body>
<h1>Lobby</h1>
<section>
    <h3>Create New Game</h3>
    <form action="/games" method="post">
        <label for="gameName">New Game Name:</label>
        <input type="text" id="gameName" placeholder="Game Name">
        <input type="submit" value="Create">
    </form>
</section>
<section>
    <h3>Existing Games</h3>
    <ul>
        <li><a href="games/GAME_ID">Some Game</a></li>
    </ul>
</section>
</body>

</html>
