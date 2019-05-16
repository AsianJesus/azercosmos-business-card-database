<?php


namespace App\Http\Middleware\Changelogger;

use App\ChangeLog;
use Closure;

class RegisterDelete
{
    public function handle($request, Closure $closure) {
        $result = $closure($request);
        ChangeLog::create([
            'type' => 'del',
            'data' => $request->id
        ]);
        return $result;
    }
}