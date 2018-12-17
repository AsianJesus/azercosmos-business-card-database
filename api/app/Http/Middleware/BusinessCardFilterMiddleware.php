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

        $data = json_decode($response->content());
        if (!$data) return $response;
        // This should be replaced
        $user_id = $request->user_id;
        $data =array_filter($data, function ($card) use($user_id) {
            return !$card->private ||
                $card->created_by == $user_id ||
                !empty(array_filter($card->permissions,function ($permission) {
                    return strtolower($permission->permission->name) == 'read';
                }));
        });
        $response->setContent(array_values($data));
        return $response;
    }
}