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
$router->get('/business-cards-one/{id}', ['middleware' => 'business_cards_filter', 'uses' => 'BusinessCardController@getOne']);
$router->post('/business-cards-one/exists', 'BusinessCardController@doesExists');
$router->get('/business-cards/{id}', 'BusinessCardController@getById');
$router->post('/business-cards/', ['middleware' => 'register_add', 'uses' => 'BusinessCardController@add']);
$router->put('/business-cards/{id}', ['uses'=>'BusinessCardController@update', 'middleware' => [ 'check_permission', 'register_update'] ]);
$router->post('/business-cards/{id}', ['uses'=>'BusinessCardController@update', 'middleware' => [ 'check_permission', 'register_update']]);
$router->delete('/business-cards/{id}', ['uses'=>'BusinessCardController@delete', 'middleware' => ['check_permission', 'register_delete'] ]);
$router->delete('/business-cards/{id}/permissions', ['uses'=>'BusinessCardController@deletePermissionOfUser',
                                                    'middleware' => 'check_permission']);
$router->get('/business-cards-excel', ['middleware' => 'business_cards_filter', 'uses' => 'BusinessCardController@exportExcel']);

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

// Synchronization routes
$router->get('/synchronize', 'ChangeLogController@getChanges');
$router->post('/synchronize', 'ChangeLogController@synchronize');
$router->post('/synchronize/passwords', 'PasswordsController@syncSetPassword');

// Password routes
$router->post('/passwords', 'PasswordsController@setPassword');
$router->post('/users', 'PasswordsController@login');
$router->get('/passwords', 'PasswordsController@checkHash');
$router->get('/user/passwords', 'PasswordsController@getByUser');

// Test routes! Needs to be deleted
$router->get('/test/synchronize', ['uses' => 'ChangeLogController@launchSynchronization']);
$router->get('/test/load-image', function () {
    dispatch(new \App\Jobs\LoadImage('images/1543582655.jpeg'));
});