<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It is a breeze. Simply tell Lumen the URIs it should respond to
| and give it the Closure to call when that URI is requested.
|
*/

$router->get('/', function () use ($router) {
    return $router->app->version();
});

//Business cards
$router->get('/b-cards', 'BusinessCardController@getAll');
$router->get('/b-cards/user/{user_id}', 'BusinessCardController@getByUser');
$router->get('/b-cards/{id}', 'BusinessCardController@getById');
$router->post('/b-cards/', 'BusinessCardController@add');
$router->put('/b-cards/{id}', 'BusinessCardController@update');
$router->delete('/b-cards/{id}', 'BusinessCardController@delete');

//Permissions
$router->get('/permissions', 'PermissionController@getAll');

//PermissionUsers
$router->get('/per-users', 'PermissionUserController@getAll');
$router->get('/per-users/{id}', 'PermissionUserController@getById');
$router->post('/per-users', 'PermissionUserController@add');
$router->put('/per-users/{id}', 'PermissionUserController@update');
$router->delete('/per-users/{id}', 'PermissionUserController@delete');