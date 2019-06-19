<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 30.11.2018
 * Time: 17:10
 */

namespace App\Http\Middleware;

use App\BusinessCard;
use App\PermissionUser;
use \Closure;

class CheckAuthenticationMiddleware
{
    public function handle($request, Closure $next){
        $user_id = app()->id;
        if (!$user_id) {
            return response('User is not authenticated', 466);
        } else {
            return $next($request);
        }
    }
};