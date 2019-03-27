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
$router->get('/business-cards', ['middleware' => 'business_cards_filter', 'uses' => 'BusinessCardController@getAll']);
$router->get('/business-cards/{id}', 'BusinessCardController@getById');
$router->post('/business-cards/', 'BusinessCardController@add');
$router->put('/business-cards/{id}', ['uses'=>'BusinessCardController@update', 'middleware' => 'check_permission']);
$router->delete('/business-cards/{id}', ['uses'=>'BusinessCardController@delete', 'middleware' => 'check_permission']);
$router->delete('/business-cards/{id}/permissions', ['uses'=>'BusinessCardController@deletePermissionOfUser',
                                                    'middleware' => 'check_permission']);

//Users
$router->get('/user/', 'UserController@getMyUser');
$router->get('/users/', 'UserController@getAll');
$router->get('/users/by-name/{name}', 'UserController@getByName');
$router->get('/users/{id}', 'UserController@getById');

//Permissions
$router->get('/permissions', 'PermissionController@getAll');

//PermissionUsers
$router->get('/user-permissions', 'PermissionUserController@getAll');
$router->get('/user-permissions/{id}', 'PermissionUserController@getById');
$router->post('/user-permissions', 'PermissionUserController@add');
$router->put('/user-permissions/{id}', 'PermissionUserController@update');
$router->delete('/user-permissions/{id}', 'PermissionUserController@delete');