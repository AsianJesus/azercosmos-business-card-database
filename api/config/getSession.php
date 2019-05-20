<?php

if (isset($_SESSION['id'])) {
    return $_SESSION['id'];
}
if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
    return \App\Helper\Helper::checkHash($_SERVER['HTTP_AUTHORIZATION']) ?? 1;
}
return 1;
// return isset($_SESSION['id']) ? $_SESSION['id'] : 1;