<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 11/29/2018
 * Time: 4:16 PM
 */

namespace App\Http\Controllers;


use App\User;
use Illuminate\Http\Request;

class UserController extends Controller
{
    protected $user;

    public function __construct(User $user)
    {
        parent::__construct($user, []);
        $this->user = $user;
    }

    public function getByName(Request $request, $name) {
        return $this->user::where('name', 'like', '%'.$name.'%')->get();
    }
}