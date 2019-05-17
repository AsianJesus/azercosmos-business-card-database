<?php


namespace App\Http\Controllers;


use App\Helper\Helper;
use App\Jobs\SynchronizePassword;
use App\Password;
use App\User;
use Illuminate\Http\Request;

class PasswordsController extends Controller
{
    protected $password;

    public function __construct(Password $password)
    {
        parent::__construct($password, []);
        $this->password = $password;
    }

    public function setPassword (Request $request) {
        $user_id = app()->id;
        $pass = $request->input('password');
        $password = $this->__setPassword($user_id, $pass)->toArray();
        $this->dispatch(new SynchronizePassword($user_id, $pass));
        unset($password['salt']);
        return $password;

    }

    public function login (Request $request) {
        $people_id = User::where('login', $request->input('login', ''))->first();
        if ($people_id == null) {
            return response('User doesn\'t exists', 401);
        }
        $id = $people_id->ID;
        $password = $this->password::where('user_id', $id)->first();
        if ($password == null) {
            return response('You hasn\'t generated password', 402);
        }
        if ($password->password != hash('sha256', $request->input('password'))) {
            return response('Passwords doesn\'t match', 403);
        }
        return [
            'key' => $id.'/'.Helper::generateHash($id, $password->salt),
            'id' => $id
            ];
    }

    public function checkHash(Request $request) {
        $parts = explode('/', $request->input('hash'));
        $user_id = $parts[0];
        $hash = $parts[1];
        $password = Password::where('user_id', $user_id)->firstOrFail();
        return [
            'match' => $hash == Helper::generateHash($user_id, $password->salt)
        ];
    }

    public function syncSetPassword(Request $request) {
        $this->__setPassword($request->input('user_id'), $request->input('password'));
    }

    private function __setPassword ($id, $pass) {
        $password = Password::where('user_id', $id)->first();
        if ($password != null) {
            $password->password = hash('sha256', $pass);
            $password->salt = Helper::generateSalt();
            $password->save();
        } else {
            $password = Password::create([
                'user_id' => $id,
                'password' => hash('sha256', $pass),
                'salt' => Helper::generateSalt()
            ]);
        }
        return $password;
    }
}