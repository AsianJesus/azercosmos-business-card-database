<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 11/27/2018
 * Time: 12:09 AM
 */

namespace App;


use Illuminate\Database\Eloquent\Model;

class PermissionUser extends Model
{
    protected $fillable = [
        'user_id', 'permission_id', 'business_card_id'
    ];

    protected $table = 'permission_user';

    public function businessCard() {
        return $this->belongsTo(BusinessCard::class, 'business_card_id');
    }
    public function user() {
        return $this->belongsTo(User::class, 'user_id');
    }
    public function permission() {
        return $this->belongsTo(Permission::class, 'permission_id');
    }
}