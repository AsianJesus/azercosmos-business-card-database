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

class CheckPermissionMiddleware
{
    public function handle($request, Closure $next){
        $user_id = $request->input('user_id', 1);
        $op = $request->method() == 'DELETE' ? 'delete' : 'edit';
        if (BusinessCard::where('id', $request->id)->firstOrFail()->created_by == $user_id ||
            PermissionUser::where('user_id', $user_id)->where('business_card_id', $request->id)->whereHas('permission',
                function($q) use($op) {
                    $q->where('name', 'LIKE', '%'.$op.'%');
            })->first()) {
            return $next($request);
        } else {
            return response('Permission denied',403);
        }
    }
};