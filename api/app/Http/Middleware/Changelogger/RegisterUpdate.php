<?php


namespace App\Http\Middleware\Changelogger;

use App\ChangeLog;
use Closure;

class RegisterUpdate
{
    public function handle($request, Closure $closure) {
        $result = $closure($request);
        ChangeLog::create([
            'type' => 'upd',
            'data' => json_encode([
                'data' => $request->all(),
                'id' => $request->id
            ])
        ]);
        return $result;
    }
}