<?php


namespace App;


use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class ChangeLog extends Model
{
    protected $fillable = [
        'type', 'data'
    ];

    protected static function boot()
    {
        parent::boot();

        self::creating(function ($log) {
            $log->{$log->getKeyName()} = Str::uuid();
        });
    }

    public function getKeyName()
    {
        return 'id';
    }

    public function getKeyType()
    {
        return 'string';
    }

    public function getIncrementing()
    {
        return false;
    }
}