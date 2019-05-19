<?php

use Illuminate\Database\Seeder;
use App\User;
use App\Password;
use App\Helper\Helper;

class FillUsersPasswords extends Seeder
{
    public function run() {
        $users = User::all();
        $password = env('DEFAULT_PASSWORD', 'password');
        foreach($users as $user) {
            Password::create([
                'salt' => Helper::generateSalt(),
                'password' => $password,
                'user_id' => $user->ID
            ]);
        }
    }
}