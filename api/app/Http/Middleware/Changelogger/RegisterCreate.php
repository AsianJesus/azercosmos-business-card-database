<?php


namespace App\Http\Middleware\Changelogger;

use App\ChangeLog;
use Closure;

class RegisterCreate
{

    public function handle($request, Closure $closure) {
        $result = $closure($request);

        $res = $result;
        $res->original->note = $request->input('note');
        unset($res->original->notes);
        ChangeLog::create([
            'type' => 'add',
            'data' => json_encode($res->original)
        ]);

return $res;
        if ($res->image_path != null) {
            ChangeLog::create([
                'type' => 'cim', // Create IMage
                'data' => $res->image_path
            ]);
        }

        return $result;
    }
}