<?php


namespace App\Helper;


use App\Password;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class Helper
{
    private const SALT_LENGTH = 64;

    static public function generateSalt() {
        $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        $charactersLength = strlen($characters);
        $randomString = '';
        for ($i = 0; $i < self::SALT_LENGTH; $i++) {
            $randomString .= $characters[rand(0, $charactersLength - 1)];
        }
        return $randomString;
    }

    static public function checkHash($hash) {
        $parts = explode('/', $hash);
        $user_id = $parts[0];
        $hash = $parts[1];
        $password = new Password();
        $password = DB::table($password->getTable())->where('user_id', $user_id)->first();
        return $hash == Helper::generateHash($user_id, $password->salt) ? $user_id : null;
    }

    static public function generateHash ($id, $salt) {
        $result = $salt;
        for ($i = 0; $i < 3; $i++) {
            $f = "$id/$result";
            $result = hash('sha256', $f);
        }
        return $result;
    }

}