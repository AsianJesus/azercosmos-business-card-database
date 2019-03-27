<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 11/27/2018
 * Time: 1:20 PM
 */

namespace App\Http\Controllers;

use App\PermissionUser;
use Illuminate\Http\Request;

class PermissionUserController extends Controller
{
    protected $permission;

    public function __construct(PermissionUser $pu)
    {
        parent::__construct($pu, []);
        $this->permission = $pu;
    }

    public function add(Request $request) {
    	return parent::add($request)->load('user');
    }
}