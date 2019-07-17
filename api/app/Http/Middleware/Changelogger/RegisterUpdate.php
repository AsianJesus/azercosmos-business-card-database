<?php


namespace App\Http\Middleware\Changelogger;

use App\ChangeLog;
use Closure;

class RegisterUpdate
{
    public function handle($request, Closure $closure) {
        $result = $closure($request);
        if ($request->file('photo')) {
            ChangeLog::create([
                'type' => 'cim', // Create IMage
                'data' => $result->original->image_path
            ]);
            $request['image_path'] = $result->original->image_path;
        }
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
