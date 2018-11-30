<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 30.11.2018
 * Time: 14:51
 */

namespace App\Http\Middleware;

use Closure;

class BusinessCardFilterMiddleware
{
    public function handle($request, Closure $next) {
        $response = $next($request);
        return $response;
        $user_id = 1;
        $data =array_filter($data, function ($card) use($user_id) {
            return !$card->private ||
                $card->created_by == $user_id ||
                !empty(array_filter($card->permissions, function ($permission) use ($user_id){
                    return strtolower($permission->permission->name) == 'read' && $permission->user_id == $user_id;
                }));
        });
        $response->setContent(array_values($data));
        return $response;
    }
}